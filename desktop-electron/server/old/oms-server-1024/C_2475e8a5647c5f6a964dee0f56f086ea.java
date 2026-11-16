import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_2475e8a5647c5f6a964dee0f56f086ea
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonFunc.unix2Date(mapRow.get("WORKTIME").toString(),"yyyy-MM-dd HH:mm");
    }
}
