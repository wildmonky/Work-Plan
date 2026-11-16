import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_47551bd77bccf561baf5f57bedc9b949
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.unix2Date(mapRow.get("CREATE_TIME").toString(),"yyyy-MM-dd HH:mm");
    }
}
