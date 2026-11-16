import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_03db1a0bf7b69759172d81518045c49f
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.unix2Date(String.valueOf(CommonFunc.unixTime()));
    }
}
