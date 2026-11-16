import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_0ea2ed83b2cf21f5ccfef378cf77bca7
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonObject.mapJCBType.get(mapRow.get("A.JTYPE"));

    }
}
